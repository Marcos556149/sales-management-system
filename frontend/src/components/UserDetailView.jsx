import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  User,
  Edit2,
  UserCog
} from 'lucide-react';
import { userService } from '../services/userService';
import './ProductDetailView.css';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { useMemo } from 'react';
import ChangeUserStatusModal from './ChangeUserStatusModal';

const UserDetailView = () => {
  const { id: userId } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false);

  const handleBack = () => {
    navigate('/dashboard/users');
  };

  const handleEdit = () => {
    navigate(
      `/dashboard/users/edit/${userId}`,
      {
        state: { user }
      }
    );
  };

  const handleChangeStatus = () => {
    setIsStatusModalOpen(true);
  };

  useKeyboardShortcuts(
    useMemo(() => ({
      'ctrl+b': handleBack,

      'e': () => {
        if (user) {
          handleEdit();
        }
      },

      'a': () => {
        if (user) {
          handleChangeStatus();
        }
      }
    }), [user])
  );

  useEffect(() => {
    let isMounted = true;

    const fetchUser = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);

      try {
        setLoading(true);
        setError(null);

        const response = await userService.getUser(
          userId,
          { signal: controller.signal }
        );

        clearTimeout(timeoutId);

        if (isMounted) {
          setUser(response.data);
        }
      } catch (err) {
        clearTimeout(timeoutId);

        if (isMounted) {
          if (err.status === 404) {
            setError('Usuario no encontrado');
          } else if (err.name === 'AbortError') {
            setError('La solicitud agotó el tiempo de espera');
          } else {
            setError(err.message || 'Error al cargar usuario');
          }
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchUser();

    return () => {
      isMounted = false;
    };
  }, [userId]);

  if (loading) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button
            className="btn-secondary"
            onClick={handleBack}
          >
            <ArrowLeft size={16} />
            <span>Volver a Usuarios</span>
            <span className="btn-shortcut">Ctrl+B</span>
          </button>

          <div className="detail-actions">
            <button
              className="btn-outline-primary whitespace-nowrap"
              onClick={handleEdit}
            >
              <Edit2 size={16} />
              <span>Editar</span>
              <span className="btn-shortcut">E</span>
            </button>

            <button
              className="btn-outline-danger whitespace-nowrap"
              onClick={handleChangeStatus}
            >
              <UserCog size={16} />
              <span>Cambiar estado</span>
              <span className="btn-shortcut">A</span>
            </button>
          </div>
        </div>

        <div
          className="detail-card"
          style={{
            padding: '48px',
            textAlign: 'center'
          }}
        >
          <p>Cargando usuario...</p>
        </div>
      </div>
    );
  }

  if (error || !user) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={handleBack}>
            <ArrowLeft size={16} />
            <span>Volver a Usuarios</span>
            <span className="btn-shortcut">Ctrl+B</span>
          </button>
        </div>

        <div className="not-found-card">
          <h2>{error || 'Usuario no encontrado'}</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="view-container">
      <div className="detail-toolbar">
        <button
          className="btn-secondary"
          onClick={handleBack}
        >
          <ArrowLeft size={16} />
          <span>Volver a Usuarios</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>

        <div className="detail-actions">
          <button
            className="btn-outline-primary whitespace-nowrap"
            onClick={handleEdit}
          >
            <Edit2 size={16} />
            <span>Editar</span>
            <span className="btn-shortcut">E</span>
          </button>

          <button
            className="btn-outline-warning whitespace-nowrap"
            onClick={handleChangeStatus}
          >
            <UserCog size={16} />
            <span>Cambiar estado</span>
            <span className="btn-shortcut">A</span>
          </button>
        </div>
      </div>

      <div className="detail-card">
        <div className="detail-header">
          <div className="detail-title-group">
            <div className="product-icon-container">
              <User size={32} className="product-main-icon" />
            </div>

            <div>
              <h2 className="detail-title">
                {user.userName}
              </h2>

              <p className="detail-subtitle">
                Usuario del sistema
              </p>
            </div>
          </div>

          <span
            className={`status-badge large-badge ${(user.userStatus?.code || '').toLowerCase()}`}
          >
            {user.userStatus?.label || 'Desconocido'}
          </span>
        </div>

        <div className="detail-body">
          <div className="info-grid">


            <div className="info-box">
              <h3 className="info-label">
                Rol
              </h3>

              <p className="info-value">
                {user.userRole?.label || 'Desconocido'}
              </p>
            </div>


          </div>
        </div>
      </div>

      <ChangeUserStatusModal
        isOpen={isStatusModalOpen}
        user={user}
        onClose={() => setIsStatusModalOpen(false)}
        onSuccess={async () => {
          const response = await userService.getUser(userId);
          setUser(response.data);
        }}
      />
    </div>
  );
};

export default UserDetailView;